package back.repository.user;

import back.model.User;
import back.repository.SpecificationProvider;
import back.repository.SpecificationProviderManager;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSpecificationProviderManager implements SpecificationProviderManager<User> {
    private final List<SpecificationProvider<User>> specificationProviderList;

    @Override
    public SpecificationProvider<User> getSpecificationProvider(String key) {
        return specificationProviderList.stream()
                .filter(sp -> sp.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Specification provider does not exist for key: %s", key)));
    }
}
