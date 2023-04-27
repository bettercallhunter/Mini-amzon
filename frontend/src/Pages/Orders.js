import React from "react";
import { useState } from "react";
import Order from "../Elements/Order";
import authHeader from "../utils/authHeader";
import { Link } from "react-router-dom";
const Orders = () => {
    const [orders, setOrders] = useState([])
    useState(() => {
        const fetchItem = async () => {
            const response = await fetch("/api/orders", {
                method: "GET",
                headers: { "Content-Type": "application/json", ...authHeader() },
                credentials: 'include'
            })
            // console.log(response)
            const order = await response.json();
            setOrders(order.content);
        }
        fetchItem();

    }, [])
    return (
        <React.Fragment>
            <h1>Orders</h1>
            {orders.length >= 1 && orders.filter(order => order.status !== "SHIPPINGCART").map(order => <Order {...order} />)}
            <Link to={"/"}>
                <button>Back</button>
            </Link>
        </React.Fragment>
    )
}
export default Orders