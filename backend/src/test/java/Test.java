//
//import org.junit.runner.RunWith;
//import org.mini_amazon.models.Warehouse;
//import org.mini_amazon.proto.WorldAmazonProtocol;
//import org.mini_amazon.test.TcpClientGateway;
//import org.mini_amazon.utils.AMessageBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import jakarta.annotation.Resource;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class Test {
//  @Autowired
//  private TcpClientGateway tcpClientGateway;
//
// @org.junit.Test
//  public void test(){
////    TcpClientGateway tcpClientGateway = context.getBean(TcpClientGateway.class);
//    List<Warehouse> warehouses = new ArrayList<>();
//    List<WorldAmazonProtocol.AInitWarehouse> wh = warehouses.stream().map(w -> WorldAmazonProtocol.AInitWarehouse.newBuilder().setX(w.getX()).setY(w.getY()).setId(w.getId()).build()).toList();
//    WorldAmazonProtocol.AConnect aConnect = AMessageBuilder.createNewWorld(null, wh);
//    tcpClientGateway.send(aConnect);
//  }
//}
