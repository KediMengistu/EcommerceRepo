package com.ecommerce.auction_service.IncomingRequestObjectBodies;

import com.ecommerce.auction_service.OtherServiceObjects.Catalog;

import java.time.LocalDate;
import java.time.LocalTime;

public class CatalogAndTimeRequestBody {
    private Catalog catalog;
    private LocalDate startDate;
    private LocalTime starttime;
    private LocalTime endtime;

    public CatalogAndTimeRequestBody() {
    }

    public CatalogAndTimeRequestBody(Catalog catalog, LocalDate startDate, LocalTime starttime, LocalTime endtime) {
        this.catalog = catalog;
        this.startDate = startDate;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalTime getStarttime() {
        return starttime;
    }

    public void setStarttime(LocalTime starttime) {
        this.starttime = starttime;
    }

    public LocalTime getEndtime() {
        return endtime;
    }

    public void setEndtime(LocalTime endtime) {
        this.endtime = endtime;
    }
}