// import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';
import React from 'react';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import './App.css';
import Buy from './Pages/Buy';
import FindOrder from './Pages/FindOrder';
import Index from './Pages/Home';
import Orders from './Pages/Orders';
import Login from './User/Login';
import Register from './User/Register';
import Cart from './Pages/Cart';
import EditOrder from './Pages/EditOrder';
function App() {
  return (
    <Router>
      <Routes>
        <Route exact path="/" element={<Index />} />
        <Route exact path="/login" element={<Login />} />
        <Route exact path="/register" element={<Register />} />
        <Route exact path="/buy/:id" element={<Buy />} />
        <Route exact path="/orders" element={<Orders />} />
        <Route exact path="/findorder" element={<FindOrder />} />
        <Route exact path="/cart" element={<Cart />} />
        <Route exact path="/editOrder/:id" element={<EditOrder />} />
      </Routes>
    </Router>
  );
}

export default App;
