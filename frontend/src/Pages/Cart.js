import React, { useEffect } from "react";
import Order from "../Elements/Order";
import { useState } from "react";
import { Navigate } from "react-router-dom";
import Checkout from "../components/Checkout";
import authHeader from "../utils/authHeader";

const Cart = (props) => {
  const [cart, setCart] = useState([]);
  const getCart = async () => {
    const response = await fetch("/api/cart", {
      method: "GET",
      headers: { "Content-Type": "application/json", ...authHeader() },
      credentials: "include",
    });
    const orders = await response.json();
    console.log(orders);
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
      {cart.length >= 0 && <Checkout props={cart} />}
    </div>
  );
};
export default Cart;
