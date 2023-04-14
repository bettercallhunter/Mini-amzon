import React from "react";
import { useState } from "react";

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const login = (e) => {
        e.preventDefault();
        console.log(username)
        console.log(password)
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