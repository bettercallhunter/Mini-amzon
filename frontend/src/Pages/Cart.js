import React, { useEffect } from "react";
import Order from "../Elements/Order";
import { useState } from "react";
import { Navigate } from "react-router-dom";
import Checkout from "../components/Checkout";
import authHeader from "../utils/authHeader";

import { Link } from "react-router-dom";
const Cart = (props) => {
    const [cart, setCart] = useState([]);
    const getCart = async () => {
        const response = await fetch("/api/cart", {
            method: "GET",
            headers: { "Content-Type": "application/json", ...authHeader() },
            credentials: "include",
        });
        const orders = await response.json();
        // console.log(orders);
        setCart(orders);
    };

    useEffect(() => {
        getCart();
    }, []);

    return (
        <div className="container">
            <h1>Cart</h1>
            {cart.map((c, index) => (
                <Order {...c} key={index} />
            ))}
            {cart.length > 0 && <Checkout cart={cart} />}
            {cart.length <= 0 && <h1>Haiya add some stuff to your cart</h1>}
            <Link to={"/"}>
                <button>Back</button>
            </Link>
        </div>
    );
};
export default Cart;
