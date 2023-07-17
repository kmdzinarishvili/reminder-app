import { useNavigate } from "react-router-dom";
import { useState } from "react";
import useAuth from "../hooks/useAuth";
import LogoutBtn from "../components/LogoutBtn";

const NavBar = () =>{

    const fetchToday = () =>{
        alert("pressed today")
    }

    const fetchTomorrow = () =>{
    }

    const fetchThisWeek =  () => {
    }
    return <div className="header">
            <button onClick={fetchToday}>Today</button>
            <button onClick={fetchTomorrow}>Tomorrow</button>
            <button onClick={fetchThisWeek}>This Week</button>
    </div>
}


const Home = () => {
    const { setAuth } = useAuth;
    const navigate = useNavigate();
    const [data, setData]= useState();
    const addReminder = () =>{
        navigate('/add');
    }
    return (
        <section className="page">
            <h1>Home</h1>
            <div className="flexGrow">
            <button onClick={addReminder}>Add Reminder</button>

                <NavBar/>

                <LogoutBtn/>
            </div>
        </section>
    )
}

export default Home
