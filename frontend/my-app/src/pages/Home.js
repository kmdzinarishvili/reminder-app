import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import useAuth from "../hooks/useAuth";
import LogoutBtn from "../components/LogoutBtn";
import axios from 'axios';
import Reminder from "../components/Reminder";
import { query } from "../helper/query";
 
const {REACT_APP_API_BASE_URL: BASE_URL} = process.env;
const OVERDUE = '/reminders/overdue';
const TODAY = '/reminders/today';
const TOMORROW = '/reminders/tomorrow';
const THIS_WEEK = '/reminders/week'

const NavBar = ({setData}) =>{

    const {auth} = useAuth();
    const fetchToday = async () =>{
        const response = await query(
            {urlExtension:TODAY,
            accessToken:auth.accessToken}
        );
        console.log('fetching tomorrow', response)
        setData(response?.data);
    }

    const fetchTomorrow = async() =>{
        const response = await query(
            {urlExtension:TOMORROW,
            accessToken:auth.accessToken}
        );
        console.log('fetching tomorrow', response)
        setData(response?.data);
    }

    const fetchThisWeek = async () => {
        const response = await query(
            {urlExtension:THIS_WEEK,
            accessToken:auth.accessToken}
        );
        console.log('fetching this week', response)
        setData(response?.data);
    }
    return <div className="row">
        <button onClick={fetchToday}>Today</button>
        <button onClick={fetchTomorrow}>Tomorrow</button>
        <button onClick={fetchThisWeek}>This Week</button>
    </div>
}


const Home = () => {
    const { auth } = useAuth();
    const navigate = useNavigate();
    const [data, setData]= useState([]);
    const [overdueReminders, setOverdueReminders] = useState([]);
    const [fetchToggle, setFetchToggle] = useState(false);
    const addReminder = () =>{
        navigate('/add');
    }
    const getOverdueReminders = async () => {
        const response = await axios.get(BASE_URL+OVERDUE,
        {
            headers: { 
                'Content-Type': 'application/json', 
                'Authorization': "Bearer "+ auth.accessToken,
            }, 
        }
        );
        setOverdueReminders(response?.data);
    }
    useEffect(()=>{
       getOverdueReminders();
    },[fetchToggle])
    return (
        <section className="page">
            <h1>Home</h1>
            <div className="flexGrow">
            <button onClick={addReminder}>Add Reminder</button>
                <h4>Overdue: </h4>
                <div>
                    {overdueReminders.map((reminder)=>
                        <Reminder key={reminder.id} reminder={reminder} setFetchToggle={setFetchToggle}/>
                    )}
                </div>
                <NavBar setData={setData}/>
                <div>
                    {data.map((reminder)=>
                        <Reminder key={reminder.id} reminder={reminder}/>
                    )}
                </div>
                <LogoutBtn/>
            </div>
        </section>
    )
}

export default Home
