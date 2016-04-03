package club.broking.cdn.servlets.orders;

import club.broking.cdn.models.Product;
import club.broking.cdn.services.CassandraService;
import club.broking.cdn.services.TokenService;
import club.broking.cdn.servlets.AbstractJsonServlet;
import com.datastax.driver.core.*;
import com.datastax.driver.mapping.MappingManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class OrdersServlet extends AbstractJsonServlet<OrdersRequest, OrdersResponse> {

    private final Session session;
    private final TokenService tokenService;

    public OrdersServlet() {
        super(OrdersRequest.class, OrdersResponse.class);

        CassandraService cassandraService = CassandraService.getInstance();
        this.session = cassandraService.getSession();
        this.tokenService = TokenService.getInstance();
    }

    @Override
    protected void doPost(OrdersRequest request, OrdersResponse response) {
        Map<String, Object> claims = this.tokenService.verify(request.token);

        if(claims == null) {
            response.result = false;
            return;
        }

        PreparedStatement prepared = this.session.prepare("SELECT id, total_price, created_at FROM shop.orders WHERE user_id = ?;");
        BoundStatement bound = new BoundStatement(prepared);
        UUID userId = UUID.fromString((String)claims.get("id"));
        ResultSet result = this.session.execute(bound.bind(userId));
        Set<Order> orders = new HashSet<Order>();

        for(Row row:result) {
            UUID orderId = row.getUUID("id");
            Product[] products = this.findProductsBy(orderId);
            Order order = new Order();
            order.totalPrice = row.getDouble("total_price");
            order.createdAt = row.getTimestamp("created_at");
            order.products = products;

            orders.add(order);
        }

        response.result = true;
        response.orders = orders.toArray(new Order[orders.size()]);
    }

    private Product[] findProductsBy(UUID orderId) {
        PreparedStatement prepared = this.session.prepare("SELECT name, price, image FROM shop.order_products WHERE order_id = ?;");
        BoundStatement bound = new BoundStatement(prepared);
        ResultSet result = this.session.execute(bound.bind(orderId));
        Set<Product> products = new HashSet<Product>();

        for(Row row:result) {
            Product product = new Product();

            product.setName(row.getString("name"));
            product.setPrice(row.getDouble("price"));
            product.setImage(row.getString("image"));

            products.add(product);
        }

        return products.toArray(new Product[products.size()]);
    }

}
