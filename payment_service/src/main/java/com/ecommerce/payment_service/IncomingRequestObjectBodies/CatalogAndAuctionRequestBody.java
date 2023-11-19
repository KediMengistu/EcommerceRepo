package com.ecommerce.payment_service.IncomingRequestObjectBodies;

import com.ecommerce.payment_service.OtherServiceObjects.Auction;
import com.ecommerce.payment_service.OtherServiceObjects.Catalog;

public class CatalogAndAuctionRequestBody {
    private Catalog catalog;
    private Auction auction;

    public CatalogAndAuctionRequestBody() {
    }

    public CatalogAndAuctionRequestBody(Catalog catalog, Auction auction) {
        this.catalog = catalog;
        this.auction = auction;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}
