package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;



@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(classes = SareetaApplication.class)
public class SareetaApplicationTests {
    @Autowired
    private CartController cartController;
    @Autowired
    private ItemController itemController;
    @Autowired
    private OrderController orderController;
    @Autowired
    private UserController userController;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void testCreateUserSuccess(){
        CreateUserRequest createUserRequest = createUserRequestSuccess();
        userController.createUser(createUserRequest);

        ResponseEntity<User> userSaved = userController.findByUserName(createUserRequest.getUsername());
        Assertions.assertEquals(createUserRequest.getUsername(), userSaved.getBody().getUsername());
    }

    @Test
    public void testCreateUserFail(){
        CreateUserRequest createUserRequest = createUserRequestFail();
        userController.createUser(createUserRequest);

        ResponseEntity<User> userSaved = userController.findByUserName(createUserRequest.getUsername());
        Assertions.assertNull(userSaved.getBody());
        Assertions.assertEquals(404, userSaved.getStatusCodeValue());
    }

    @Test
    public void testGetItemNotFound(){
        ResponseEntity<Item> item = itemController.getItemById(1000L);
        Assertions.assertNull(item.getBody());
        Assertions.assertEquals(404, item.getStatusCodeValue());
    }

    @Test
    public void testGetItemById(){
        ResponseEntity<Item> item = itemController.getItemById(1L);
        Assertions.assertEquals(item.getBody().getId(), 1L);
        Assertions.assertEquals(200, item.getStatusCodeValue());
    }

    @Test
    public void testAddToCart(){
        CreateUserRequest createUserRequest = createUserRequestSuccess();
        ResponseEntity<User> userSaved = userController.createUser(createUserRequest);
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(userSaved.getBody().getUsername());
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(5);

        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals(5, response.getBody().getItems().size());
    }

    @Test
    public void testRemoveFromCart(){
        CreateUserRequest createUserRequest = createUserRequestSuccess();
        ResponseEntity<User> userSaved = userController.createUser(createUserRequest);
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(userSaved.getBody().getUsername());
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(5);

        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        ModifyCartRequest cartRemoveRequest = new ModifyCartRequest();
        cartRemoveRequest.setUsername(userSaved.getBody().getUsername());
        cartRemoveRequest.setItemId(1L);
        cartRemoveRequest.setQuantity(2);

        ResponseEntity<Cart> responseRemove = cartController.removeFromcart(cartRemoveRequest);
        Assertions.assertEquals(200, responseRemove.getStatusCodeValue());
        Assertions.assertEquals(3, response.getBody().getItems().size());
    }

    @Test
    public void testSubmitOrder(){
        CreateUserRequest createUserRequest = createUserRequestSuccess();
        ResponseEntity<User> userSaved = userController.createUser(createUserRequest);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(userSaved.getBody().getUsername());
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(5);
        cartController.addTocart(cartRequest);

        ResponseEntity<UserOrder> userOrderResponse = orderController.submit(userSaved.getBody().getUsername());
        Assertions.assertEquals(200, userOrderResponse.getStatusCodeValue());
        Assertions.assertEquals(5, userOrderResponse.getBody().getItems().size());
    }

    @Test
    public void testGetOrders(){
        CreateUserRequest createUserRequest = createUserRequestSuccess();
        ResponseEntity<User> userSaved = userController.createUser(createUserRequest);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(userSaved.getBody().getUsername());
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(5);
        cartController.addTocart(cartRequest);

        orderController.submit(userSaved.getBody().getUsername());
        ResponseEntity<List<UserOrder>> userOrderResponse = orderController.getOrdersForUser(userSaved.getBody().getUsername());
        Assertions.assertEquals(1, userOrderResponse.getBody().size());
        Assertions.assertEquals(200, userOrderResponse.getStatusCodeValue());
    }

    public CreateUserRequest createUserRequestSuccess(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("tulx");
        createUserRequest.setPassword("123456789");
        createUserRequest.setConfirmPassword("123456789");
        return createUserRequest;
    }

    public CreateUserRequest createUserRequestFail(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("tulx");
        createUserRequest.setPassword("123456789");
        createUserRequest.setConfirmPassword("123");
        return createUserRequest;
    }


}
