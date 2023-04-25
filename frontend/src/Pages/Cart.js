import React from "react";
import Order from "../Elements/Order";
import { useState } from "react";
import { Navigate } from "react-router-dom";
import Checkout from "../components/Checkout";


const Cart = props => {
    const [cart, setCart] = useState([])
    const getCart = async () => {
        const response = await fetch("/api/cart", {
            method: "GET",
            headers: { "Content-Type": "application/json" },
            credentials: 'include'
        });
        const orders = await response.json();
        setCart(orders);
    }

    getCart();
    return (
        <React.Fragment>
            <h1>Cart</h1>
            {cart.length >= 1 && cart.map(cart => <Order {...cart} />)}
            {cart.length >= 0 && <Checkout props={cart} />}

        </React.Fragment>
    )


}
export default Cart;