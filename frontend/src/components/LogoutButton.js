import React from "react";

const LogoutButton = () => {
  let token = localStorage.getItem("token");
  console.log(token);
  const onLogout = () => {
    localStorage.removeItem("token");
    alert("You have been logged out");
    window.location.reload();
  };
  return (
    <div>
      {token && (
        <button onClick={onLogout} className="btn btn-primary">
          Logout
        </button>
      )}
    </div>
  );
};

export default LogoutButton;
