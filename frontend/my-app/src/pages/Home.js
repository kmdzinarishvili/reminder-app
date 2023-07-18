import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import useAuth from "../hooks/useAuth";
import LogoutBtn from "../components/LogoutBtn";
import axios from 'axios';
import Reminder from "../components/Reminder";
import { query } from "../helper/query";
 
const {REACT_APP_API_BASE_URL: BASE_URL} = process.env;

const USER = '/user'

const OVERDUE = '/reminders/overdue';
const OLD = '/reminders/old';
const TODAY = '/reminders/today';
const TOMORROW = '/reminders/tomorrow';
const THIS_WEEK = '/reminders/week';

const CREATION = '/creation';
const PRIORITY = '/priority';


const NavBar = ({setWhichFetch, howManyDays}) =>{
    const fetchOld = async () =>{
        setWhichFetch("OLD")
    }
    const fetchToday = async () =>{
        setWhichFetch("TODAY")
    }
    const fetchTomorrow = async() =>{
        setWhichFetch("TOMORROW")
    }
    const fetchThisWeek = async () => {
        setWhichFetch("WEEK")
    }
    return <div className="row">
        <button className="nav-item" onClick={fetchOld}>Previous {howManyDays} days</button>
        <button className="nav-item" onClick={fetchToday}>Today</button>
        <button className="nav-item" onClick={fetchTomorrow}>Tomorrow</button>
        <button className="nav-item" onClick={fetchThisWeek}>This Week</button>
    </div>
}


const Home = () => {
    const { auth } = useAuth();
    const navigate = useNavigate();
    const [data, setData]= useState([]);
    const [overdueReminders, setOverdueReminders] = useState([]);
    const [orderBy, setOrderBy] = useState(PRIORITY);
    const [whichFetch, setWhichFetch] = useState("TODAY");
    const [fetchToggle, setFetchToggle] = useState(false);
    const [userData, setUserData] = useState([]);
    const addReminder = () =>{
        navigate('/add');
    }

    const fetchOld = async () =>{
        const response = await query(
            {urlExtension:OLD,
            accessToken:auth.accessToken}
        );
        console.log('fetching today', response)
        setData(response?.data);
    }

    const fetchToday = async (extension) =>{
        console.log(extension);
        const response = await query(
            {urlExtension:TODAY+extension,
            accessToken:auth.accessToken}
        );
        console.log('fetching today', response)
        setData(response?.data);
    }

    const fetchTomorrow = async(extension) =>{
        const response = await query(
            {urlExtension:TOMORROW+extension,
            accessToken:auth.accessToken}
        );
        console.log('fetching tomorrow', response)
        setData(response?.data);
    }

    const fetchThisWeek = async (extension) => {
        const response = await query(
            {urlExtension:THIS_WEEK+extension,
            accessToken:auth.accessToken}
        );
        console.log('fetching this week', response)
        setData(response?.data);
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
        if(whichFetch==="TODAY"){
            fetchToday(orderBy);
        }else if (whichFetch ==="TOMORROW"){
            fetchTomorrow(orderBy);
        }else if (whichFetch ==="WEEK"){
            fetchThisWeek(orderBy);
        }else{
            fetchOld();
        }
    },[whichFetch,fetchToggle,orderBy])

    useEffect(()=>{
       getOverdueReminders();
    },[fetchToggle])

    
    const getUserData = async () => {
        const response = await axios.get(BASE_URL+USER,
            {
                headers: { 
                    'Content-Type': 'application/json', 
                    'Authorization': "Bearer "+ auth.accessToken,
                }, 
            }
        );
        setUserData(response.data);
    }

    useEffect(()=>{
        getUserData();
    },[])

    const editProfile = (e) =>{
        e.preventDefault()
        navigate("/profile", {state:{userData}})
    }

    return (
        <section className="page">
            <img onClick={editProfile} className="user-icon icon" src="user_icon.png" alt="profile"/>
            <h1 className="reminders-title">Reminders</h1>
            <div className="items-container">
                {overdueReminders.length>0&&<h4>Overdue: </h4>}
                <div>
                    {overdueReminders?.map((reminder)=>
                        <Reminder key={reminder.id} reminder={reminder} setFetchToggle={setFetchToggle}/>
                    )}
                </div>
                <NavBar setWhichFetch={setWhichFetch} howManyDays={userData.daysBeforeReminderDelete}/>
                <div className="cont">
                    <button className="add-btn" onClick={addReminder}>
                        <img className="small-icon" src="plus_icon.png" alt="edit"/>
                        Add Reminder
                    </button>
                    {whichFetch!=="OLD"&&
                    <div className="order-by">
                        <p>Order by:</p>
                        <input
                        style={{marginTop:20}}
                        type="radio"
                        name="orderby"
                        value="PRIORITY"
                        id="priority"
                        checked={orderBy === PRIORITY}
                        onChange={()=>setOrderBy(PRIORITY)}
                        />
                        <label htmlFor="regular">Priority</label>
                        <input
                        style={{marginTop:20}}
                        type="radio"
                        name="orderby"
                        value="CREATION"
                        id="creation"
                        checked={orderBy === CREATION}
                        onChange={()=>setOrderBy(CREATION)}
                        />
                        <label htmlFor="regular">Creation Date</label>
                    </div>}
                </div>
                <div>
                    {data.map((reminder)=>
                        <Reminder key={reminder.id} reminder={reminder} setFetchToggle={setFetchToggle}/>
                    )}
                </div>
            </div>
        </section>
    )
}

export default Home
