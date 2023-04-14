import React from "react";
import { useState } from "react";
import { Navigate } from "react-router-dom";
const Register = () => {



    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const register = async (e) => {
        e.preventDefault();
        const response = await fetch("http://localhost:8080/register", {
            method: "POST",
            // headers: {
            //     'Content-Type': 'application/json'
            // },
            body: JSON.stringify({
                username: username,
                email: email,
                password: password
            })

        })
        const data = await response.json();
        console.log(data)
        console.log(username)
        console.log(password)
        console.log(email)

        return < Navigate to="/login" />

    }

    return (
        <React.Fragment>
            <h1>Register</h1>

            <form onSubmit={register}>
                <input type="text" placeholder="Username" value={username} onChange={ev => setUsername(ev.target.value)} />
                <input type="email" placeholder="Email" value={email} onChange={ev => setEmail(ev.target.value)} />
                <input type="password" placeholder="Password" value={password} onChange={ev => setPassword(ev.target.value)} />
                <button type="submit">Login</button>
            </form>
        </React.Fragment>
    )


}
export default Register