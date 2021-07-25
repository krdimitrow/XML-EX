package com.example.xmlex;

import com.example.xmlex.model.dto.*;
import com.example.xmlex.service.CategoryService;
import com.example.xmlex.service.ProductService;
import com.example.xmlex.service.UserService;
import com.example.xmlex.util.XmlParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private static final String RESOURCE_FILES_PATH = "src/main/resources/files/";
    private static final String OUTPUT_FILES_PATH = "out/";
    private static final String CATEGORIES_FILES_NAME = "categories.xml";
    private static final String USERS_FILES_NAME = "users.xml";
    private static final String PRODUCTS_FILES_NAME = "products.xml";
    private static final String PRODUCTS_IN_RANGE_FILE_NAME = "products-in-range.xml";
    private static final String SOLD_PRODUCTS_FILE_NAME = "sold-products.xml";

    private final XmlParser xmlParser;
    private final CategoryService categoryService;
    private final UserService userService;
    private final ProductService productService;
    private final BufferedReader bufferedReader;


    public CommandLineRunnerImpl(XmlParser xmlParser, CategoryService categoryService, UserService userService, ProductService productService) {
        this.xmlParser = xmlParser;
        this.categoryService = categoryService;
        this.userService = userService;
        this.productService = productService;
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    }


    @Override
    public void run(String... args) throws Exception {
        seedData();

        System.out.println("Select ex: ");
        int exNum = Integer.parseInt(bufferedReader.readLine());

        switch (exNum){
            case 1 -> productsInRange();
            case 2 -> userWithSoldProducts();
        }


    }

    private void userWithSoldProducts() throws JAXBException {
        UserViewRootDto userViewRootDto = userService.findUsersWithMoreThanOneSolProduct();

        xmlParser.writeToFile(RESOURCE_FILES_PATH+OUTPUT_FILES_PATH+SOLD_PRODUCTS_FILE_NAME,userViewRootDto);
    }

    private void productsInRange() throws JAXBException {

        ProductViewRootDto rootDto =productService.findProductInRangeWithNoBuyer();

        xmlParser.writeToFile(RESOURCE_FILES_PATH + OUTPUT_FILES_PATH + PRODUCTS_IN_RANGE_FILE_NAME,
                rootDto);
    }

    private void seedData() throws JAXBException, FileNotFoundException {
        if (categoryService.getEntityCount() == 0) {
            CategorySeedRootDto categorySeedRootDto = xmlParser.fromFile(RESOURCE_FILES_PATH + CATEGORIES_FILES_NAME, CategorySeedRootDto.class);
            categoryService.seedCategories(categorySeedRootDto.getCategories());
        }


        if (userService.getCount() == 0) {
            UserSeedRootDto userSeedRootDto = xmlParser.fromFile(RESOURCE_FILES_PATH + USERS_FILES_NAME, UserSeedRootDto.class);


            userService.seedUsers(userSeedRootDto.getUsers());
            System.out.println();
        }

        if (productService.getCount() == 0) {
            ProductSeedRootDto productSeedRootDto = xmlParser.fromFile(RESOURCE_FILES_PATH + PRODUCTS_FILES_NAME,ProductSeedRootDto.class);


            productService.seedProducts(productSeedRootDto.getProducts());

        }


    }
}
