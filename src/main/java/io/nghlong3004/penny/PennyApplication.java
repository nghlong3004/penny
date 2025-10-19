package io.nghlong3004.penny;

import io.nghlong3004.penny.service.PennyService;
import io.nghlong3004.penny.util.ObjectContainer;

public class PennyApplication {
    public static void main(String[] args) {
        PennyService penny = ObjectContainer.getPennyService();
        penny.run();
    }
}