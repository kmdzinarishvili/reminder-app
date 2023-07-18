import React, { useEffect } from 'react';
import { useState } from 'react';
import useAuth from '../hooks/useAuth';
import { post } from '../helper/post';
import { useLocation, useNavigate } from 'react-router-dom';
import { timeZones } from '../consts/timeZones';
import Labels from '../components/Labels';
import axios from 'axios';
import FileUploader from '../components/FileUploader';
const {REACT_APP_API_BASE_URL: BASE_URL} = process.env;
const padWithLeadingZeros = (num) =>{
    return String(num).padStart(2, '0');
}

const EditProfile = () =>{
    const { state } = useLocation();
    const {userData} = state;

    const navigate = useNavigate();
    const [username, setUsername] = useState(userData.username);
    const [timeZone, setTimeZone] = useState(userData.timezoneOffsetHours);
    const {email} = userData;
    const [numDays, setNumDays] = useState(userData.daysBeforeReminderDelete);
    const { auth } = useAuth();
    const [errMsg, setErrMsg]=useState();

    const handleSubmit= async (e) => {
      e.preventDefault();
      const data = {username,timezoneOffsetHours:timeZone,daysBeforeReminderDelete: numDays};
      await post({urlExtension:`/user/update`,
          body:data,
          accessToken:auth.accessToken
      }).then(res => {
        navigate("/")
      }).catch(err =>{
        if(err.response.data){
          setErrMsg("User with this identifier not found");
        }else {
          setErrMsg("Please enter all requried fields");
        }
      });
      
    }

    const deleteAccount = async(e)=>{
      e.preventDefault();
    }
  
    return (
      <div className="parent">
      <form className="container" onSubmit={e => { handleSubmit(e) }}>
        <h1 className="title">Account Details</h1>
        <img  className="large-icon" src="user_icon.png" alt="profile"/>
        <label className='label'>Username:</label>
        <input 
          className='edit input editInput'
          name='username' 
          type='text'
          value={username}
          onChange={e => setUsername(e.target.value)}
        />
        <br/>
        <label className='label'>Email:</label>
        <input 
          className='edit input editInput'
          name='username' 
          type='text'
          value={email}
          readOnly
        />
        <br />
        <label className='label'>Time Zone (UTC offset):</label>
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
        <label className='label'>Days to show old reminders:</label>
        <input
          className='edit input editInput'
          name='numDays' 
          type='number'
          min={1}
          value={numDays}
          onChange={e => setNumDays(e.target.value)}
        />
        <br/>       
    
        {errMsg}
        
        <div className='button-container'>
          <button className='btn'>
          <img onClick={deleteAccount}className="delete small-icon" src="trash_icon.png" alt="delete"/>
            Delete Account
          </button>
          <input 
            className='btn'
            type='submit' 
            value='Save Changes' 
          />
        </div>
   
      </form>
      </div>
    )
  }

export default EditProfile;


