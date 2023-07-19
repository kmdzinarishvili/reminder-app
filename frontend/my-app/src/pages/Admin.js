import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import useAuth from "../hooks/useAuth";
import LogoutBtn from "../components/LogoutBtn";
import { query } from "../helper/query";
import User from "../components/User";
 
const ADMIN = '/admin';

const USERNAME ='/username';
const REGISTRATION = '/registration';
const ACTIVITY ='/activity';


const NavBar = ({setWhichFetch}) =>{

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
        Order By: 
        <button className="nav-item" onClick={fetchByUsername}>Username</button>
        <button className="nav-item" onClick={fetchByRegistration}>Registration Date</button>
        <button className="nav-item"onClick={fetchByLastActifity}>Last Activity</button>
    </div>
}


const Admin = () => {
    const { auth, setAuth } = useAuth();
    const [data, setData]= useState([]);
    const [whichFetch, setWhichFetch] = useState(USERNAME);
    const [fetchToggle, setFetchToggle] = useState(false);
    const navigate = useNavigate();

    const fetchUsers= async (fetchType) =>{
        console.log(USERNAME);
        const response = await query(
            {urlExtension:ADMIN+fetchType,
            accessToken:auth.accessToken}
        ).catch(err =>{
            if(err?.response?.status===403){
                setAuth({});
                navigate('/login');
            }
        });
        setData(response?.data);
    }

    useEffect(()=>{
        fetchUsers(whichFetch);
    },[whichFetch, fetchToggle])

    return (
        <section className="page">
            <div style={{position:'absolute',right:20, top:20}}>
                <LogoutBtn style={{width:100, height:40}}/>
            </div>
            <h1 className="reminders-title">Admin Page</h1>
                <NavBar setWhichFetch={setWhichFetch} />
                   <div className="contain">
                   <table >
                   <tr>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Registration Date</th>
                        <th>Date of Last Activity</th>
                        <th>Delete</th>
                    </tr>
                    {data.map((user)=>{
                       return <User user={user} setFetchToggle={setFetchToggle}/>
                    })}
                    </table>
                
            </div>
        </section>
    )
}

export default Admin
