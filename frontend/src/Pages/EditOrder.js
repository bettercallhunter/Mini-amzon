import React, { useState } from "react";
import { Navigate, useParams } from "react-router-dom";
import authHeader from "../utils/authHeader";
import { Link } from "react-router-dom";
const EditOrder = (props) => {
    const { id } = useParams();
    const [x, setX] = useState(0);
    const [y, setY] = useState(0);
    const [redirect, setRedirect] = useState(false);
    const handleChangeAddress = async (ev) => {
        ev.preventDefault();
        const response = await fetch("/api/editShipment", {
            method: "POST",
            body: JSON.stringify({
                destinationX: parseInt(x),
                destinationY: parseInt(y),
                orderId: parseInt(id)
            }),
            headers: { "Content-Type": "application/json", ...authHeader() },
            credentials: 'include'
        })
        if (response.ok) {
            alert("Address Changed Successfully")
            setRedirect(true);

        }
    }
    if (redirect) {

        return (< Navigate to="/orders" />)
    }
    return (
        <div>
            <form onSubmit={handleChangeAddress}>
                <label htmlFor="X">new destination X</label>
                <input type="number" id="X" placeholder="X" value={props.x} onChange={ev => setX(ev.target.value)} />
                <br />
                <label htmlFor="Y">new destination Y</label>
                <input type="number" id="Y" placeholder="Y" value={props.y} onChange={ev => setY(ev.target.value)} />
                <button type="submit">Change Address</button>
            </form>
            <Link to={"/orders"}>
                <button>Back</button>
            </Link>
        </div>
    )

};
export default EditOrder;