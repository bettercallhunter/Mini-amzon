import React, { useState } from "react";
import { Link, Navigate, useParams } from 'react-router-dom';
import authHeader from "../utils/authHeader";
const Buy = () => {
    const { id } = useParams();
    const [quantity, setQuantity] = useState(0);
    const [redirect, setRedirect] = useState(false);
    const [x, setX] = useState('');
    const [y, setY] = useState('');
    const [ups, setUps] = useState('');

    const buy = async (e) => {
        e.preventDefault();
        const response = await fetch('http://localhost:8000/api/cart', {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...authHeader()
            },
            // body: JSON.stringify({ quantity, x, y }),
            body: JSON.stringify({ quantity, id }),
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

            <h1>Add to Cart</h1>
            <form onSubmit={buy}>
                <label htmlFor="quantity">Quantity</label>
                <input type="number" id="quantity" placeholder="Quantity" value={quantity} onChange={ev => setQuantity(ev.target.value)} />
                <br />
                <label htmlFor="x">Address: X</label>
                <input type="text" placeholder="Address:X" id="x" value={x} onChange={ev => setX(ev.target.value)} />
                <label htmlFor="y">Address: Y</label>
                <input type="text" placeholder="Address:Y" id="y" value={y} onChange={ev => setY(ev.target.value)} />
                <br />
                <label htmlFor="cardNumber">Card Number: </label>
                <input type="text" placeholder="Card" id="cardNumber" />
                <br />
                <label htmlFor="ups">UPS Account: </label>
                <input type="text" placeholder="UPS Account Name" id="ups" value={ups} onChange={ev => setUps(ev.target.value)} />
                <br />
                <button type="submit">add to cart</button>
            </form>
            <Link to={"/"}>
                <button>Back</button>
            </Link>
        </React.Fragment>
    )

}
export default Buy