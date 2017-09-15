package by.kraskovski.pms.controller;

import by.kraskovski.pms.controller.dto.CartDto;
import by.kraskovski.pms.domain.model.Cart;
import by.kraskovski.pms.security.exception.UserNotFoundException;
import by.kraskovski.pms.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceAlreadyExistsException;

/**
 * Controller for the {@link by.kraskovski.pms.service.CartService}.
 */
@RestController
@RequestMapping("/cart")
@Slf4j
public class CartController {

    private final CartService cartService;
    private final Mapper mapper;

    @Autowired
    public CartController(final CartService cartService, final Mapper mapper) {
        this.cartService = cartService;
        this.mapper = mapper;
    }

    /**
     * Find cart by id.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity loadCartById(@PathVariable("id") final String id) {
        log.info("Start loadCartById: " + id);
        try {
            final Cart cart = cartService.find(id);
            Assert.notNull(cart, "Unable to find cart with id: " + id);
            return new ResponseEntity<>(mapper.map(cart, CartDto.class), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Create empty cart.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<Object> createCart(@PathVariable("id") final String id) {
        log.info("Start createCart for user with id: {}", id);
        try {
            cartService.create(id);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserNotFoundException | InstanceAlreadyExistsException | DataIntegrityViolationException e) {
            log.info("Error in createCart. " + e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Add productStock to user cart.
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity addProductToCart(
            @RequestParam("cart_id") final String cartId,
            @RequestParam("ps_id") final String productStockId,
            @RequestParam(value = "count", required = false, defaultValue = "1") final int count) {
        log.info("start addProductStock: {} to cart: {} with count: {}", productStockId, cartId, count);
        return cartService.addProduct(cartId, productStockId, count)
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Delete product from cart by id.
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteProductFromCart(
            @RequestParam("cart_id") final String cartId,
            @RequestParam("ps_id") final String productStockId,
            @RequestParam(value = "count", required = false, defaultValue = "1") final int count) {
        log.info("start deleteProductFromCart: {} from cart: {} with count: {}", productStockId, cartId, count);
        return cartService.deleteProduct(cartId, productStockId, count)
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Delete cart by id.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteCart(@PathVariable("id") final String id) {
        log.info("Start deleteCart");
        try {
            cartService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            log.info("Error in deleteCart. " + e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
