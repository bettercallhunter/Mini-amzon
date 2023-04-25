import React, { useState } from "react";
import { Navigate } from "react-router-dom";
const Checkout = props => {
    const [X, setX] = useState(0);
    const [Y, setY] = useState(0);
    const [redirect, setRedirect] = useState(false);
    const checkout = async (e) => {
        e.preventDefault();
        const response = await fetch("/api/checkout", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ X, Y }, props.cart),
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
                <input type="text" id="X" placeholder="X" value={X} onChange={ev => setX(ev.target.value)} />
                <br />
                <label htmlFor="Y">Y: </label>
                <input type="text" id="Y" placeholder="Y" value={Y} onChange={ev => setY(ev.target.value)} />
                <br />
                <button type="submit">Checkout</button>
            </form >
        </React.Fragment>
    )

}
export default Checkout;