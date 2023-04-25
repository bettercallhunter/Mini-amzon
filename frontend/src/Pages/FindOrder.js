import React from "react";
import { useState, useEffect } from "react";
import Order from "../Elements/Order";

const FindOrder = () => {
    const [shipmentNumber, setShipmentNumber] = useState('');
    const [orders, setOrders] = useState(null);
    // need to modify here
    // useEffect(() => {
    //     if (orders !== null && orders.length >= 1) {
    //         return (
    //             <React.Fragment>
    //                 {orders.map(order => <Order {...order} />)}
    //             </React.Fragment>
    //         )
    //     }
    // }, []);

    const findOrder = async (e) => {
        e.preventDefault();
        const response = await fetch(`/api/findShipment`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ shipmentNumber }),
            credentials: 'include'
        });


        if (response.status !== 200) {
            alert('Item not found');
        }
        else {
            const orderFound = await response.json();
            setOrders(orderFound);

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
        </React.Fragment>
    )
}
export default FindOrder;