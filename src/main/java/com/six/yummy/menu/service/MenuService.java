package com.six.yummy.menu.service;

import com.six.yummy.global.exception.NotFoundMenuException;
import com.six.yummy.global.exception.NotFoundRestaurantException;
import com.six.yummy.global.exception.NotFoundUserException;
import com.six.yummy.menu.entity.Menu;
import com.six.yummy.menu.repository.MenuRepository;
import com.six.yummy.menu.requestdto.MenuRequest;
import com.six.yummy.menu.responsedto.MenuListResponse;
import com.six.yummy.menu.responsedto.MenuResponse;
import com.six.yummy.restaurant.entity.Restaurant;
import com.six.yummy.restaurant.repository.RestaurantRepository;
import com.six.yummy.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public MenuResponse saveMenu(MenuRequest menuRequest, Long restaurantId, Long userId) {

        validationUser(userId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            NotFoundRestaurantException::new
        );

        Menu menu = menuRepository.save(Menu.createMenu(
            menuRequest.getMenuName(),
            menuRequest.getMenuPrice(),
            menuRequest.getMenuContents(),
            menuRequest.getCategory(),
            restaurant));

        return MenuResponse.builder()
            .menuId(menu.getMenuId())
            .menuContents(menu.getMenuContents())
            .menuPrice(menu.getMenuPrice())
            .menuName(menu.getMenuName())
            .category(menu.getCategory())
            .build();
    }

    @Transactional(readOnly = true)
    public List<MenuListResponse> getMenus(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
            NotFoundRestaurantException::new
        );

        return menuRepository.findByRestaurant(restaurant).stream().map(
                (Menu menu) -> new MenuListResponse(menu.getMenuName(), menu.getMenuPrice()))
            .toList();
    }

    public MenuResponse getMenu(Long restaurantId, Long menuId, Long userId) {
        validationUser(userId);
        validationRestaurant(restaurantId);
        Menu menu = findMenuById(menuId);
        return new MenuResponse(menu.getMenuId(), menu.getMenuName(), menu.getMenuPrice(),
            menu.getMenuContents(), menu.getCategory());
    }

    @Transactional
    public MenuResponse updateMenu(Long menuId, MenuRequest menuRequest, Long restaurantId,
        Long userId) {

        validationUser(userId);
        validationRestaurant(restaurantId);

        Menu menu = findMenuById(menuId);

        menu.update(menuRequest.getMenuName(), menuRequest.getMenuPrice(),
            menuRequest.getMenuContents(), menuRequest.getCategory());

        return MenuResponse.builder()
            .menuId(menu.getMenuId())
            .menuContents(menu.getMenuContents())
            .menuPrice(menu.getMenuPrice())
            .menuName(menu.getMenuName())
            .category(menu.getCategory())
            .build();
    }

    public void deleteMenu(Long menuId, Long restaurantId, Long userId) {

        validationUser(userId);
        validationUser(restaurantId);

        Menu menu = findMenuById(menuId);

        menuRepository.delete(menu);
    }

    private void validationUser(Long userId) {
        userRepository.findById(userId).orElseThrow(
            NotFoundUserException::new
        );
    }

    private void validationRestaurant(Long restaurantId) {
        restaurantRepository.findById(restaurantId).orElseThrow(
            NotFoundRestaurantException::new
        );
    }

    private Menu findMenuById(Long menuId) {
        return menuRepository.findById(menuId).orElseThrow(
            NotFoundMenuException::new
        );
    }

}
