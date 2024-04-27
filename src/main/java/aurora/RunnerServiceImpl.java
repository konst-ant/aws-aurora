package aurora;

import aurora.model.User;
import aurora.repository.ItemRepository;
import aurora.repository.UserRepository;
import aurora.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RunnerServiceImpl implements RunnerService {

    private int counter = 0;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Scheduled(fixedDelay = 1000) // Call every second
    @Transactional
    public void infiniteRun() {

        counter++;

        Item someItem = new Item("Some Item " + counter);
        itemRepository.save(someItem);
        Item otherItem = new Item("Other Item " + counter);
        itemRepository.save(otherItem);

        User someUser = new User("John Smith " + counter);
        someUser.getBoughtItems().add(someItem); // Link
        someItem.setBuyer(someUser); // Link
        someUser.getBoughtItems().add(otherItem);
        otherItem.setBuyer(someUser);
        userRepository.save(someUser);

        Item unsoldItem = new Item("Unsold Item " + counter);
        itemRepository.save(unsoldItem);
        System.out.println("Successfully saved User (John Smith " + counter + ") and his items");

//        Item item = itemRepository.findById(someItem.getId()).get();
//        Item item2 = itemRepository.findById(unsoldItem.getId()).get();
    }
}
