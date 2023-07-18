import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import useAuth from "../hooks/useAuth";
import LogoutBtn from "../components/LogoutBtn";
import axios from 'axios';
import Reminder from "../components/Reminder";
import { query } from "../helper/query";
 
const {REACT_APP_API_BASE_URL: BASE_URL} = process.env;

const ADMIN = '/admin';

const USERNAME ='/username';
const REGISTRATION = '/registration';
const ACTIVITY ='/activity';


const NavBar = ({setWhichFetch, howManyDays}) =>{

    const fetchByUsername = async () =>{
        setWhichFetch(USERNAME)
    }
    const fetchByRegistration = async() =>{
        setWhichFetch(REGISTRATION)
    }
    const fetchByLastActifity = async () => {
        setWhichFetch(ACTIVITY)
    }
    return <div className="row">
        <button onClick={fetchByUsername}>Username</button>
        <button onClick={fetchByRegistration}>Registration Date</button>
        <button onClick={fetchByLastActifity}>Last Activity</button>
    </div>
}


const Admin = () => {
    const { auth } = useAuth();
    const navigate = useNavigate();
    const [data, setData]= useState([]);
    const [whichFetch, setWhichFetch] = useState(USERNAME);
    const [fetchToggle, setFetchToggle] = useState(false);
    const [userData, setUserData] = useState([]);

    const fetchUsers= async (fetchType) =>{
        console.log(USERNAME);
        const response = await query(
            {urlExtension:ADMIN+fetchType,
            accessToken:auth.accessToken}
        );
        console.log('fetching by ', response)
        setData(response?.data);
    }

    useEffect(()=>{
        fetchUsers(whichFetch);
    },[whichFetch,fetchToggle])

    return (
        <section className="page">
            <h1>Admin Page</h1>
            <div className="flexGrow">
                Order By: <NavBar setWhichFetch={setWhichFetch} howManyDays={userData.daysBeforeReminderDelete}/>
                   <div className="App">
                    {data.map((item)=>{
                       return <p key={item.username}>{item.username}</p>
                    })}
                    {/* {data.map((reminder)=>
                        <Reminder key={reminder.id} reminder={reminder} setFetchToggle={setFetchToggle}/>
                    )} */}
                </div>
                <LogoutBtn/>
            </div>
        </section>
    )
}

export default Admin
