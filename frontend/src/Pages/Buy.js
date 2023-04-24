import React from "react";
import { useState } from "react";
import { useParams } from 'react-router-dom'
import { Navigate } from "react-router-dom";
import { Link } from "react-router-dom";
const Buy = () => {
    const { id } = useParams();
    const [quantity, setQuantity] = useState(0);
    const [address, setAddress] = useState("");
    const [redirect, setRedirect] = useState(false);
    const buy = async (e) => {
        e.preventDefault();
        const response = await fetch(`/api/buy/${id}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ quantity, address }),
            credentials: 'include'
        });
        console.log(quantity);

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
            <h1>Buy</h1>
            <form onSubmit={buy}>
                <label htmlFor="quantity">Quantity</label>
                <input type="number" id="quantity" placeholder="Quantity" value={quantity} onChange={ev => setQuantity(ev.target.value)} />
                <br />
                <label htmlFor="address">Address</label>
                <input type="text" placeholder="Address" id="address" value={address} onChange={ev => setAddress(ev.target.value)} />
                <br />
                <label htmlFor="cardNumber">Card Number: </label>
                <input type="text" placeholder="Card" id="cardNumber" />
                <br />
                <button type="submit">Buy</button>
            </form>
            <Link to={"/"}>
                <button>Back</button>
            </Link>
        </React.Fragment>
    )

}
export default Buy