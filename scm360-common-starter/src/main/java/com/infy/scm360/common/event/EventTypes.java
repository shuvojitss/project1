package com.infy.scm360.common.event;

public final class EventTypes {
    private EventTypes() {}

    // Catalog Events
    public static final String CATALOG_PRODUCT_CREATED = "catalog.product.created";
    public static final String CATALOG_PRODUCT_UPDATED = "catalog.product.updated";
    public static final String CATALOG_PRODUCT_DISCONTINUED = "catalog.product.discontinued";

    // Supplier Events
    public static final String SUPPLIER_APPROVED = "supplier.approved";
    public static final String SUPPLIER_SUSPENDED = "supplier.suspended";
    public static final String SUPPLIER_SCORECARD_UPDATED = "supplier.scorecard.updated";
    public static final String SUPPLIER_PO_ACKNOWLEDGED = "supplier.po.acknowledged";
    public static final String SUPPLIER_ASN_SUBMITTED = "supplier.asn.submitted";

    // Procurement Events
    public static final String PROCUREMENT_REQUISITION_CREATED = "procurement.requisition.created";
    public static final String PROCUREMENT_PO_APPROVED = "procurement.po.approved";
    public static final String PROCUREMENT_PO_ISSUED = "procurement.po.issued";
    public static final String PROCUREMENT_PO_CANCELLED = "procurement.po.cancelled";
    public static final String PROCUREMENT_PO_CLOSED = "procurement.po.closed";

    // Warehouse Events
    public static final String WAREHOUSE_GOODS_RECEIVED = "warehouse.goods.received";
    public static final String WAREHOUSE_PUTAWAY_COMPLETED = "warehouse.putaway.completed";
    public static final String WAREHOUSE_ORDER_PICKED = "warehouse.order.picked";
    public static final String WAREHOUSE_ORDER_PACKED = "warehouse.order.packed";
    public static final String WAREHOUSE_TRANSFER_DISPATCHED = "warehouse.transfer.dispatched";
    public static final String WAREHOUSE_TRANSFER_RECEIVED = "warehouse.transfer.received";
    public static final String WAREHOUSE_COUNT_VARIANCE_APPROVED = "warehouse.count.variance.approved";

    // Inventory Events
    public static final String INVENTORY_STOCK_CHANGED = "inventory.stock.changed";
    public static final String INVENTORY_STOCK_LOW = "inventory.stock.low";
    public static final String INVENTORY_STOCK_RESERVED = "inventory.stock.reserved";
    public static final String INVENTORY_RESERVATION_FAILED = "inventory.reservation.failed";
    public static final String INVENTORY_RESERVATION_RELEASED = "inventory.reservation.released";
    public static final String INVENTORY_STOCK_ADJUSTED = "inventory.stock.adjusted";

    // Order Events
    public static final String ORDER_PLACED = "order.placed";
    public static final String ORDER_CONFIRMED = "order.confirmed";
    public static final String ORDER_BACKORDERED = "order.backordered";
    public static final String ORDER_CANCELLED = "order.cancelled";
    public static final String ORDER_SHIPPED = "order.shipped";
    public static final String ORDER_DELIVERED = "order.delivered";

    // Transportation Events
    public static final String TRANSPORT_SHIPMENT_CREATED = "transport.shipment.created";
    public static final String TRANSPORT_SHIPMENT_DISPATCHED = "transport.shipment.dispatched";
    public static final String TRANSPORT_SHIPMENT_DELAYED = "transport.shipment.delayed";
    public static final String TRANSPORT_SHIPMENT_DELIVERED = "transport.shipment.delivered";
    public static final String TRANSPORT_DELIVERY_FAILED = "transport.delivery.failed";

    // Domain Exchanges
    public static final String EXCHANGE_CATALOG = "catalog.events";
    public static final String EXCHANGE_SUPPLIER = "supplier.events";
    public static final String EXCHANGE_PROCUREMENT = "procurement.events";
    public static final String EXCHANGE_INVENTORY = "inventory.events";
    public static final String EXCHANGE_WAREHOUSE = "warehouse.events";
    public static final String EXCHANGE_ORDER = "order.events";
    public static final String EXCHANGE_TRANSPORT = "transport.events";
    public static final String EXCHANGE_DLX = "scm.dlx";
}
