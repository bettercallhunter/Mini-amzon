import React from "react";
import { useState } from "react";
import Order from "../Order";
import { Link } from "react-router-dom";
const Orders = () => {
    const [orders, setOrders] = useState([])
    useState(() => {
        const fetchItem = async () => {
            const response = await fetch("http://localhost:8000/api/orders", {
                method: "GET",
                headers: { "Content-Type": "application/json" },
                credentials: 'include'
            })
            const orders = await response.json();
            setOrders(orders);
        }
        fetchItem();

    }, [])
    return (
        <React.Fragment>
            <h1>Orders</h1>
            {orders.length >= 1 && orders.map(orders => <Order {...orders} />)}
            <Link to={"/"}>
                <button>Back</button>
            </Link>
        </React.Fragment>
    )
}
export default Orders