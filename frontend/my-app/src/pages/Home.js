import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import useAuth from "../hooks/useAuth";
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


function NavBar({ setWhichFetch, style }) {
    const fetchOld = async () =>{
        setWhichFetch("OLD")
    }
    const fetchToday = async () =>{
        setWhichFetch(TODAY)
    }
    const fetchTomorrow = async() =>{
        setWhichFetch(TOMORROW)
    }
    const fetchThisWeek = async () => {
        setWhichFetch(THIS_WEEK)
    }
    return <div className="row" style={style}>
        <button className="nav-item" onClick={fetchOld}>Recently Completed</button>
        <button className="nav-item" onClick={fetchToday}>Today</button>
        <button className="nav-item" onClick={fetchTomorrow}>Tomorrow</button>
        <button className="nav-item" onClick={fetchThisWeek}>This Week</button>
    </div>
}


function Home() {
    const { auth, setAuth } = useAuth();
    const navigate = useNavigate();
    const [data, setData]= useState([]);
    const [overdueReminders, setOverdueReminders] = useState([]);
    const [orderBy, setOrderBy] = useState(PRIORITY);
    const [whichFetch, setWhichFetch] = useState(TODAY);
    const [fetchToggle, setFetchToggle] = useState(false);
    const [userData, setUserData] = useState([]);
    const addReminder = () =>{
        navigate('/add');
    }

    const fetchOld = async () =>{
        const response = await query(
            {urlExtension:OLD,
            accessToken:auth.accessToken}
        ).catch(err =>{
            if(err?.response?.status===403){
                setAuth({});
                navigate('/login');
            }
        });
        let reminders = response?.data?.map(el => {
            return {...el,
                isCompleted:true
            }
        })
        setData(reminders);
    }

    const fetchData = async (base,extension)=>{
        const response = await query(
            {urlExtension:base+extension,
            accessToken:auth.accessToken}
        ).catch(err =>{
            if(err?.response?.status===403){
                setAuth({});
                navigate('/login');
            }
        });
        setData(response?.data);
    }

    const getOverdueReminders = async () => {
        const response = await axios.get(BASE_URL+OVERDUE,
        {
            headers: { 
                'Content-Type': 'application/json', 
                'Authorization': "Bearer "+ auth.accessToken,
            }, 
        }).catch(err =>{
            if(err?.response?.status===403){
                setAuth({});
                navigate('/login');
            };
          });
        setOverdueReminders(response?.data);
    }

    useEffect(()=>{
        if(whichFetch==="OLD"){
            fetchOld();
        }else{
            fetchData(whichFetch, orderBy)
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
        ).catch(err =>{
            if(err?.response?.status===403){
                setAuth({});
                navigate('/login');
            };
          });;
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
                <div className="reminder-cont">
                    {overdueReminders.length>0&&<h4>Overdue: </h4>}
                            {overdueReminders?.map((reminder)=>
                            <Reminder key={reminder.id} reminder={reminder} setFetchToggle={setFetchToggle}/>
                        )}
                    </div>
                        <NavBar style={{alignSelf:'center'}}setWhichFetch={setWhichFetch} howManyDays={userData.daysBeforeReminderDelete}/>
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
               
            
            </div>
            <div className="reminder-cont">
                    {data.map((reminder)=>
                        <Reminder key={reminder.id} reminder={reminder} setFetchToggle={setFetchToggle} isCompleted={reminder.isCompleted}/>
                    )}
            </div>
        </section>
    )
}

export default Home
