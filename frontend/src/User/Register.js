import React from "react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [password2, setPassword2] = useState("");
  const navigate = useNavigate();
  //   const [redirect, setRedirect] = useState(false);
  const register = async (e) => {
    e.preventDefault();
    const response = await fetch("/api/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, email, password, password2 }),
      credentials: "include",
    });
    if (response.status === 200) {
      alert("registration successful");
      //   setRedirect(true);
      navigate("/login");
    } else {
      alert("registration failed");
    }
  };
  //   if (redirect) {
  //     return <Navigate to="/login" />;
  //   }

  return (
    // <form onSubmit={register}>
    //     <input type="text" placeholder="Username" value={username} onChange={ev => setUsername(ev.target.value)} />
    //     <input type="email" placeholder="Email" value={email} onChange={ev => setEmail(ev.target.value)} />
    //     <input type="password" placeholder="Password" value={password} onChange={ev => setPassword(ev.target.value)} />
    //     <button type="submit">Login</button>
    // </form>
    <div className="container">
      <div className="row">
        <div className="col-sm-9 col-md-7 col-lg-5 mx-auto">
          <div className="card border-0 shadow rounded-3 my-5">
            <div className="card-body p-4 p-sm-5">
              <h5 className="card-title text-center mb-5 fw-light fs-5">
                Sign Up
              </h5>

              <form onSubmit={register}>
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="username"
                    name="username"
                    value={username}
                    onChange={(ev) => setUsername(ev.target.value)}
                    required
                  />
                  <label htmlFor="username">Username</label>
                </div>
                <div className="form-floating mb-3">
                  <input
                    type="email"
                    className="form-control"
                    id="email"
                    name="email"
                    value={email}
                    onChange={(ev) => setEmail(ev.target.value)}
                    required
                  />
                  <label htmlFor="email">Email</label>
                </div>
                <div className="form-floating mb-3">
                  <input
                    type="password"
                    className="form-control"
                    id="password"
                    name="password"
                    value={password}
                    onChange={(ev) => setPassword(ev.target.value)}
                    required
                  />
                  <label htmlFor="password">Password</label>
                </div>
                <div className="form-floating mb-3">
                  <input
                    type="password"
                    className="form-control"
                    id="password2"
                    name="password2"
                    value={password2}
                    onChange={(ev) => setPassword2(ev.target.value)}
                    required
                  />
                  <label htmlFor="password2">Re-enter your password</label>
                </div>

                <p>
                  By creating an account you agree to our
                  <a href="#">Terms & Privacy</a>.
                </p>
                <div className="d-grid">
                  <button
                    className="btn btn-primary btn-login text-uppercase fw-bold"
                    type="submit"
                  >
                    Register
                  </button>
                </div>
                <div className="d-grid">
                  <p>
                    Already have an account?<a href="/login/">Login</a>
                  </p>
                </div>
                <hr className="my-4" />
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default Register;
