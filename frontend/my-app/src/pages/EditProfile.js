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
    
// Timezone (default UTC).
// Username.
// Email (view only, not editable).
// Number of days to keep completed reminders.
    const { state } = useLocation();
    const {userData} = state;

    const navigate = useNavigate();
    const [username, setUsername] = useState(userData.username);
    const [timeZone, setTimeZone] = useState("+0:00");
    const {email} = userData;
    const [numDays, setNumDays] = useState(userData.daysBeforeReminderDelete);
    const { auth } = useAuth();
    const [errMsg, setErrMsg]=useState();

    useEffect(()=>{
        console.log(userData.timeZoneOffset);
        console.log(timeZone);
    },[timeZone])
    const handleSubmit= async (e) => {
      e.preventDefault();
    //   console.log(title, recurrence, date, time, priority, category, labels)
    //   const data = {title,recurrence,date,time,priority,category,labels};
    //   await post({urlExtension:`/reminders/${reminder.id}/update`,
    //       body:data,
    //       accessToken:auth.accessToken
    //   }).then(res => {
    //     navigate("/")
    //   }).catch(err =>{
    //     if(err.response.data){
    //       setErrMsg("User with this identifier not found");
    //     }else {
    //       setErrMsg("Please enter all requried fields");
    //     }
    //   });
      
    }
  
    return (
      <form onSubmit={e => { handleSubmit(e) }}>
        <label>Username:</label>
        <br />
        <input 
          name='username' 
          type='text'
          value={username}
          onChange={e => setUsername(e.target.value)}
        />
        <br/>
        <label>Email:</label>
        <br />
        <input 
          name='username' 
          type='text'
          value={email}
          readOnly
        />
        <br />
        <label>Time Zone:</label>
        <br />
        <span>UTC: </span> 
        <select 
            id="offset"
            name="offset"
            value={timeZone}
            onChange={e=> setTimeZone(e.target.value)}>
            {Object.keys(timeZones).map((key)=>{
               return <option key={key} value={timeZones[key]}>{key}</option>
            })}
            {timeZone}
            {/* <option value="NEVER">Never</option>
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="MONTHLY">Monthly</option> */}
        </select>
        <br />
        <label>Priority:</label>
        <br />
        <input
          name='numDays' 
          type='number'
          min={1}
          value={numDays}
          onChange={e => setNumDays(e.target.value)}
        />
        <br/>       
    
        {errMsg}
        <br/>
        <input 
          type='submit' 
          value='Add Reminder' 
        />
      </form>
    )
  }

export default EditProfile;


