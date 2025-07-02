module bookkeeping.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires jakarta.validation;
    requires org.slf4j;
    requires itextpdf;
    requires java.desktop;

    opens com.bookkeeping to javafx.fxml;
    opens com.bookkeeping.controller to javafx.fxml;
    opens com.bookkeeping.model to org.hibernate.orm.core, javafx.base;
    opens com.bookkeeping.entity to org.hibernate.orm.core, javafx.base;
    opens com.bookkeeping.dao to org.hibernate.orm.core;
    opens com.bookkeeping.util to org.hibernate.orm.core;

    exports com.bookkeeping;
    exports com.bookkeeping.controller;
    exports com.bookkeeping.model;
    exports com.bookkeeping.entity;
    exports com.bookkeeping.dao;
    exports com.bookkeeping.util;
}
