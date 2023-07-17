import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import useAuth from "../hooks/useAuth";
import LogoutBtn from "../components/LogoutBtn";
import axios from 'axios';
import Reminder from "../components/Reminder";
 
const {REACT_APP_API_BASE_URL} = process.env;

const NavBar = () =>{

    const {auth} = useAuth();
    const fetchToday = async () =>{
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
    const { auth } = useAuth();
    const navigate = useNavigate();
    const [data, setData]= useState();
    const [overdueReminders, setOverdueReminders] = useState([]);
    const addReminder = () =>{
        navigate('/add');
    }
    const getOverdueReminders = async () => {
        const response = await axios.get('http://localhost:8080/api/v1/reminders/overdue',
        {
            headers: { 
                'Content-Type': 'application/json', 
                'Authorization': "Bearer "+ auth.accessToken,
            }, 
        }
        );
        console.log(response);
        setOverdueReminders(response?.data);
    }
    useEffect(()=>{
       getOverdueReminders();
    },[])
    return (
        <section className="page">
            <h1>Home</h1>
            <div className="flexGrow">
            <button onClick={addReminder}>Add Reminder</button>
                <h4>Overdue: </h4>
                <div>
                    {overdueReminders.map((item)=>
                        <Reminder item={item}/>
                    )}
                </div>
                <NavBar/>

                <LogoutBtn/>
            </div>
        </section>
    )
}

export default Home
