import React, { useState } from "react";
import { Link, Navigate, useParams } from 'react-router-dom';
import authHeader from "../utils/authHeader";
const Buy = () => {
    const { id } = useParams();
    const [quantity, setQuantity] = useState(0);
    const [redirect, setRedirect] = useState(false);


    const addToCart = async (e) => {
        e.preventDefault();
        const response = await fetch('/api/cart', {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                // "Acess-Control-Allow-Origin": "*",
                ...authHeader()
            },
            // body: JSON.stringify({ quantity, x, y }),
            body: JSON.stringify({ quantity, id }),
            credentials: 'include'
        });
        const data = await response.text();
        console.log(data);

        if (response.status === 200) {
            alert('Add to Cart Successfully');
            setRedirect(true);
        }else{
            alert('Error');
        }
    }
    if (redirect) {
        return <Navigate to='/' />
    }
    return (
        <React.Fragment>

            <h1>Add to Cart</h1>
            <form onSubmit={addToCart}>
                <label htmlFor="quantity">Quantity</label>
                <input type="number" id="quantity" placeholder="Quantity" value={quantity} onChange={ev => setQuantity(ev.target.value)} />
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