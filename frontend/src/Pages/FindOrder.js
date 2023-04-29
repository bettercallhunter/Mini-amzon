import React from "react";
import { useState, useEffect } from "react";
import Order from "../Elements/Order";
import authHeader from "../utils/authHeader";

const FindOrder = () => {
    const [shipmentNumber, setShipmentNumber] = useState('');
    const [orders, setOrders] = useState(null);

    // useEffect(() => {

    //     findOrder();
    // }, []);

    const findOrder = async (e) => {
        e.preventDefault();

        try {
            const response = await fetch('/api/findShipment', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    ...authHeader()
                },
                body: JSON.stringify({ shipmentNumber }),
                credentials: 'include'
            });

            if (response.status !== 200) {
                alert('Item not found');
                setOrders(null);
            } else {
                const data = await response.json();
                setOrders(data);
            }
        } catch (error) {
            console.error(error);
        }



    }

    return (
        <React.Fragment>
            <h1>Find Order</h1>
            <form onSubmit={findOrder}>
                <label htmlFor="shipmentNumber">Shipment Number: </label>
                <input type="text" id="shipmentNumber" placeholder="Shipment Number" value={shipmentNumber} onChange={ev => { setShipmentNumber(ev.target.value) }} />
                <br />
                <button type="submit">Find</button>
            </form>
            {orders && orders.map(order => <Order {...order} />)}
        </React.Fragment>
    )
}
export default FindOrder;