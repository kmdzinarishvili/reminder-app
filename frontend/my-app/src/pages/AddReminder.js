import React, { useEffect } from 'react';
import { useState } from 'react';
import useAuth from '../hooks/useAuth';
import { post } from '../helper/post';
import { useNavigate } from 'react-router-dom';

import Labels from '../components/Labels';
import axios from 'axios';
const {REACT_APP_API_BASE_URL: BASE_URL} = process.env;


const AddReminder = ({setFetchToggle}) =>{
    const navigate = useNavigate();

    const [title, setTitle] = useState('');
    const [recurrence, setRecurrence] = useState('NEVER');
    const [date, setDate] = useState();
    const [time, setTime] = useState();
    const [priority, setPriority] = useState();
    const [category, setCategory] = useState('WORK');
    const [showUsername, setShowUsername] = useState(false);
    const [toSomeone, setToSomeone] = useState('');
    const [labels, setLabels] = useState();
    const [attachment, setAttachment] = useState();
    const { auth } = useAuth();
    
    const handleSubmit= async (e) => {
      e.preventDefault();
      console.log(title, recurrence, date, time, priority, category, labels)
      // const response = await post({urlExtension: null,
      //     body:{title,recurrence,date,time,priority,category,labels},
      //     accessToken:auth.accessToken
      // });
      const response = await axios.post(BASE_URL+"/reminders",
        {title,recurrence,date,time,priority,category,labels},
        {
            headers: { 
                'Content-Type': 'application/json', 
                'Authorization': "Bearer "+ auth.accessToken,
        }, 
        withCredentials:true
        } 
      );
      if (response.status===200){
        alert('successfully added')
        navigate("/")
        // setFetchToggle(prev=>!prev);
      }
      // text.includes("world");
        // let formData = new FormData();
        // formData.append('file', this.file);
        // console.log('>> formData >> ', formData);
    //     axios.post('http://localhost:8080/restapi/fileupload',
    //     formData, {
    //       headers: {
    //         'Content-Type': 'multipart/form-data'
    //       }
    //     }
    //   ).then(function () {
    //     console.log('SUCCESS!!');
    //   })
    //   .catch(function () {
    //     console.log('FAILURE!!');
    //   });
    }
  
    return (
      <form onSubmit={e => { handleSubmit(e) }}>
        <label>Title:</label>
        <br />
        <input 
          name='title' 
          type='text'
          value={title}
          onChange={e => setTitle(e.target.value)}
        />
        <br/>
        <label>Recurrence:</label>
        <br />
        <select 
            id="recurrence"
            name="recurrence"
            value={recurrence}
            onChange={e=> setRecurrence(e.target.value)}>
            <option value="NEVER">Never</option>
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="MONTHLY">Monthly</option>
        </select>
        <br />
        <label>Cateogry:</label>
        <br />
        <select 
            id="category"
            name="category"
            value={category}
            onChange={e=> setCategory(e.target.value)}>
            <option value="WORK">Work</option>
            <option value="PERSONAL">Personal</option>
            <option value="EDUCATION">Education</option>
        </select>
        <br />
        <label>Date:</label>
        <br />
        <input
          name='date' 
          type='date'
          value={date}
          onChange={e => setDate(e.target.value)}
        />
        <br/>
        <input
          name='time' 
          type='time'
          value={time}
          onChange={e => setTime(e.target.value)}
        />
        <br/>
        <label>Priority:</label>
        <br />
        <input
          name='priority' 
          type='number'
          min={1}
          value={priority}
          onChange={e => setPriority(e.target.value)}
        />
        <br/>
        <div>
        <input 
            type="checkbox"
            id="show"
            name="show" 
            value ={showUsername}
            onChange={e => {
                console.log(e.target.value);
                setShowUsername(prev=> !prev)
            }} 

        />
        <label>Assign To Someone Else</label>
        </div>
        {showUsername&&<input 
          name='title' 
          type='text'
          value={toSomeone}
          onChange={e => setToSomeone(e.target.value)}
        />
        }
        <br/>
        <label>Labels:</label>
        <Labels setLabels={setLabels}/>
        <input 
          type='submit' 
          value='Add Reminder' 
        />
      </form>
    )
  }

export default AddReminder;