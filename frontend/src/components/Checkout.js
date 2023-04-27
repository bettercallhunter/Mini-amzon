import React, { useState } from "react";
import { Navigate } from "react-router-dom";
import authHeader from "../utils/authHeader";
const Checkout = props => {
    const [X, setX] = useState(0);
    const [Y, setY] = useState(0);
    const [redirect, setRedirect] = useState(false);
    const [ups, setUps] = useState('');
    const { cart } = props;
    const orders = cart.map((order) => ({ itemId: order.item.id, quantity: order.quantity, id: order.id }));


    const checkout = async (e) => {
        e.preventDefault();
        console.log(X);
        const response = await fetch("/api/placeShipment", {
            method: "POST",
            headers: { "Content-Type": "application/json", ...authHeader() },
            body: JSON.stringify({
                destinationX: parseInt(X),
                destinationY: parseInt(Y),
                upsName: ups,
                orderRequests: orders
            }),
            credentials: 'include'
        });
        if (response.status === 200) {
            alert('Item purchased successfully');
            setRedirect(true);
        }
    }
    if (redirect) {
        return <Navigate to='/' />
    }

    return (

        <React.Fragment>
            <h1>Checkout</h1>
            <form onSubmit={checkout}>
                <label htmlFor="X">X: </label>
                <input type="number" id="X" placeholder="X" value={X} onChange={ev => setX(ev.target.value)} />
                <br />
                <label htmlFor="Y">Y: </label>
                <input type="number" id="Y" placeholder="Y" value={Y} onChange={ev => setY(ev.target.value)} />
                <br />

                <label htmlFor="cardNumber">Card Number: </label>
                <input type="text" placeholder="Card" id="cardNumber" />
                <br />
                <label htmlFor="ups">UPS Account: </label>
                <input type="text" placeholder="UPS Account Name" id="ups" value={ups} onChange={ev => setUps(ev.target.value)} />
                <br />
                <button type="submit">Checkout</button>
            </form >
        </React.Fragment>
    )

}
export default Checkout;