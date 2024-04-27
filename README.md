### Spike application to elaborate AWS Aurora blue-green deployment


> Note: Aurora blue-green deployment is the smoothest option for your DB migration. You can do major DB version upgrade (and more) with high safety, minimum downtime, and no change in application code.
> For more details see AWS video: https://www.youtube.com/watch?v=6oEuBziEGKk

The app is a simple database application, simulating DB workload with infinite DB writing loop, scheduled to perform each 1 second.

### How to run

These are the steps to try the Aurora blue-green deployment with this app:
1. Create AWS Aurora cluster in your Amazon account in Web-console
 - Under RDS - Database select Aurora (PostgreSQL compatible)
 - create a custom parameter group, set `rds.logical_replication` enabled, and set replication params as AWS advised (https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/AuroraPostgreSQL.Replication.Logical.html#AuroraPostgreSQL.Replication.Logical.Configure). For example, I was able to create and make successfull switchover postgres v14 -> v16 with this set of values: max_replication_slots=10, max_wal_senders=10, max_logical_replication_workers=10, max_worker_processes=20, rds.logical_replication=1
 - choose some older engine version, e.g. v14
 - set cluster identifier `aurora`, and set initial DB name also `aurora`
 - set password manually for `postgres` DB account
 - under `Instance configuration` select Serverless V2 to reduce your bill amount
 - set Minimum and Maximum ACU to a minimum (I set 2 - 8 though could be less), we wouldn't need anyhow big resources for our DB at all
 - choose to create Mutli-AZ replica, for it to be a cluster
 - choose `Public access` option, our DB will go with public IP
 - choose appropriate security group, which would open 5432 port
 - turn off all excessive things: monitoring, extra authentication, encryption, etc.
 - set backup retention to 1 day (minimum) to keep it unpaid
 - all the rest default, or reasonable
2. Change `application.yml` with your appropriate DB connection string and credentials, and start the app. The flow will be continuously creating new records in your Aurora DB
3. In Amazon console create blue-green deployment, with target DB, postgres v16 in this case
4. In Amazon console make switchover to green cluster. For this example the `experienced downtime` for running application was `less than 2 minutes`
5. Wait till status `Switchover complete` in Amazon console. Note you may need to re-login. In my case I was getting `Switching over` hanging status, left overnight. After re-login I saw correct `Switchover complete` status
6. Delete blue cluster, if not needed
