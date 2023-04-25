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
      body: JSON.stringify({ username, email, password }),
      credentials: "include",
    });
    if (response.status === 200) {
      alert("registration successful");
    //   setRedirect(true);
      navigate("/login")
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
    <div class="container">
      <div class="row">
        <div class="col-sm-9 col-md-7 col-lg-5 mx-auto">
          <div class="card border-0 shadow rounded-3 my-5">
            <div class="card-body p-4 p-sm-5">
              <h5 class="card-title text-center mb-5 fw-light fs-5">Sign Up</h5>

              <form onSubmit={register}>
                <div class="form-floating mb-3">
                  <input
                    type="text"
                    class="form-control"
                    id="username"
                    name="username"
                    value={username}
                    onChange={(ev) => setUsername(ev.target.value)}
                    required
                  />
                  <label for="username">Username</label>
                </div>
                <div class="form-floating mb-3">
                  <input
                    type="email"
                    class="form-control"
                    id="email"
                    name="email"
                    value={email}
                    onChange={(ev) => setEmail(ev.target.value)}
                    required
                  />
                  <label for="email">Email</label>
                </div>
                <div class="form-floating mb-3">
                  <input
                    type="password"
                    class="form-control"
                    id="password"
                    name="password"
                    value={password}
                    onChange={(ev) => setPassword(ev.target.value)}
                    required
                  />
                  <label for="password">Password</label>
                </div>
                <div class="form-floating mb-3">
                  <input
                    type="password"
                    class="form-control"
                    id="password2"
                    name="password2"
                    value={password2}
                    onChange={(ev) => setPassword2(ev.target.value)}
                    required
                  />
                  <label for="password2">Re-enter your password</label>
                </div>

                <p>
                  By creating an account you agree to our
                  <a href="#">Terms & Privacy</a>.
                </p>
                <div class="d-grid">
                  <button
                    class="btn btn-primary btn-login text-uppercase fw-bold"
                    type="submit"
                  >
                    Register
                  </button>
                </div>
                <div class="d-grid">
                  <p>
                    Already have an account?<a href="/user/login/">Login</a>
                  </p>
                </div>
                <hr class="my-4" />
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default Register;
