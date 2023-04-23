package org.mini_amazon.controllers;

import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.Shipment;
import org.mini_amazon.proto.WorldAmazonProtocol;
import org.mini_amazon.services.ShipmentService;
import org.mini_amazon.socket_servers.AmazonDaemon;
import org.mini_amazon.utils.AMessageBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api")
public class ShipmentController {
  @Resource
  private AmazonDaemon amazonDaemon;
  @Resource
  private ShipmentService shipmentService;


  record ShipmentRequest(int destinationX, int destinationY,
                         List<OrderController.OrderRequest> orderRequests) {
  }

  @PostMapping("/placeShipment")
  public ResponseEntity<Shipment> placeShipment(@RequestBody ShipmentRequest request) throws ServiceError {
    Shipment shipment = shipmentService.createShipment(request.orderRequests(), request.destinationX(), request.destinationY());
    long seqNum = amazonDaemon.getSeqNum();
    List<WorldAmazonProtocol.AProduct> products = new ArrayList<>();
    for (Order order : shipment.getOrders()) {
      WorldAmazonProtocol.AProduct.Builder productBuilder = WorldAmazonProtocol.AProduct.newBuilder();
      productBuilder.setId(order.getItem().getId());
      productBuilder.setCount(order.getQuantity());
      productBuilder.setDescription(order.getItem().getDescription());
      products.add(productBuilder.build());
    }
    amazonDaemon.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(shipment.getWarehouse().getId(), products, seqNum)), seqNum);
    return ResponseEntity.ok().body(shipment);
  }


}
