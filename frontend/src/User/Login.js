import React from "react";
import { useState } from "react";

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const login = async (e) => {
        e.preventDefault();
        const response = await fetch("http://localhost:8000/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
            credentials: 'include'
        })
    }
    return (
        <React.Fragment>
            <h1>Login</h1>

            <form onSubmit={login}>
                <input type="text" placeholder="Username" value={username} onChange={ev => setUsername(ev.target.value)} />
                <input type="password" placeholder="Password" value={password} onChange={ev => setPassword(ev.target.value)} />
                <button type="submit">Login</button>
            </form>
        </React.Fragment>
    )
}
export default Login