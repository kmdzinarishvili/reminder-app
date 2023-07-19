import React from 'react';
import { useState } from 'react';
import useAuth from '../hooks/useAuth';
import { post } from '../helper/post';
import { useLocation, useNavigate } from 'react-router-dom';
import { timeZones } from '../consts/timeZones';
import axios from 'axios';
import LogoutBtn from '../components/LogoutBtn';
const {REACT_APP_API_BASE_URL: BASE_URL} = process.env;
const USER = "/user"


const EditProfile = () =>{
    const { state } = useLocation();
    const {userData} = state;

    const navigate = useNavigate();
    const [username, setUsername] = useState(userData.username);
    const [timeZone, setTimeZone] = useState(userData.timezoneOffsetHours);
    const {email} = userData;
    const [numDays, setNumDays] = useState(userData.daysBeforeReminderDelete);
    const { auth, setAuth } = useAuth();
    const [errMsg, setErrMsg]=useState();

    const isUnchanged = () =>{
      return username ===  userData.username && timeZone === userData.timezoneOffsetHours && numDays === userData.daysBeforeReminderDelete;
    }
    const handleSubmit= async (e) => {
      e.preventDefault();
      if(isUnchanged()){
        setErrMsg("No changes were made");
        return;
      }
      const data = {username,timezoneOffsetHours:timeZone,daysBeforeReminderDelete: numDays};
      await post({urlExtension:`/user/update`,
          body:data,
          accessToken:auth.accessToken
      }).then(res => {
        navigate("/")
      }).catch(err =>{
        console.log(err);
        if(err.response.data){
          setErrMsg(err.response.data);
        }else {
          setErrMsg("Please enter all requried fields");
        }
      });
      
    }

    const deleteAccount = async(e)=>{
      e.preventDefault();
      const response = await axios.delete(BASE_URL+USER,
          {
              headers: { 
                  'Content-Type': 'application/json', 
                  'Authorization': "Bearer "+ auth.accessToken,
          }, 
          withCredentials:true
          } 
      ).catch(err =>{
        if(err?.response?.status===403){
            setAuth({});
            navigate('/login');
        };
      });
      if (response?.status===200){
          setAuth({});
          navigate('/register');
      }
    }
  
    return (
      <div className="parent">
      <form className="container"  onSubmit={e => { handleSubmit(e) }}>
        <h1 className="title">Account Details</h1>
        <img  className="large-icon" src="user_icon.png" alt="profile"/>
        <label className='label'>Username</label>
        <input 
          className='input '
          name='username' 
          type='text'
          value={username}
          onChange={e => setUsername(e.target.value)}
        />
        <br/>
        <label className='label'>Email</label>
        <input 
          className='input  read-only'
          name='username' 
          type='text'
          value={email}
          onChange={(e)=>{e.preventDefault()}}
          readOnly
        />
        <br />
        <label className='label'>Time Zone (UTC offset)</label>
        <select  className='input select'
            id="offset"
            name="offset"
            value={timeZone}
            onChange={e=> setTimeZone(e.target.value)}>
            {Object.keys(timeZones).map((key)=>{
               return <option key={key} value={timeZones[key]}>{key}</option>
            })}
        </select>
        <br />
        <label className='label'>Days to save old reminders</label>
        <input
          className='input '
          name='numDays' 
          type='number'
          min={1}
          value={numDays}
          onChange={e => setNumDays(e.target.value)}
        />
        <br/>       
        {errMsg}
        <br/>   
        <br/>         
        <div className='button-container'>
          <button onClick={deleteAccount} className='btn pink'>
            <img className="delete small-icon" src="trash_icon.png" alt="delete"/>
              Delete Account
          </button>
          <input 
            className='btn'
            type='submit' 
            value='Save Changes' 
          />
        </div>
        <LogoutBtn/>
      </form>
    
      </div>
    )
  }

export default EditProfile;


