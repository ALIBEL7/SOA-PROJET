package com.hotel.room;

import jakarta.xml.ws.Endpoint;

public class ChambreServicePublisher {

    public static void main(String[] args) {
        String url = "http://localhost:8081/ChambreService";
        ChambreServiceImpl impl = new ChambreServiceImpl();

        Endpoint.publish(url, impl);

        System.out.println("✅ Service SOAP ChambreService lancé sur : " + url + "?wsdl");
        System.out.println("Appuie sur Ctrl+C pour arrêter.");
    }
}
