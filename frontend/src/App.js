import React from 'react';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import './App.css';
import Buy from './Pages/Buy';
import Index from './Pages/Index';
import Orders from './Pages/Orders';
import Login from './User/Login';
import Register from './User/Register';
import FindOrder from './Pages/FindOrder';
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
      </Routes>
    </Router>
  );
}

export default App;
