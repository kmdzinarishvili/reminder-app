import React, { useEffect } from 'react';
import { useState } from 'react';
import useAuth from '../hooks/useAuth';
import { post } from '../helper/post';
import { useLocation, useNavigate } from 'react-router-dom';

import Labels from '../components/Labels';
import { padWithLeadingZeros } from '../helper/padWithLeadingZeros';

const EditReminder = () =>{
    const { state } = useLocation();
    const {reminder} = state;
    const navigate = useNavigate();
    const [title, setTitle] = useState(reminder.title);
    const [recurrence, setRecurrence] = useState(reminder.recurrence);
    const [date, setDate] = useState(`${reminder.date[0]}-${padWithLeadingZeros(reminder.date[1])}-${padWithLeadingZeros(reminder.date[2])}`);
    const [time, setTime] = useState(`${padWithLeadingZeros(reminder.date[3])}:${padWithLeadingZeros(reminder.date[4])}`);
    const [priority, setPriority] = useState(reminder.priority);
    const [category, setCategory] = useState(reminder.category);
    const [labels, setLabels] = useState(reminder.labels);
    const { auth } = useAuth();
    const [errMsg, setErrMsg]=useState();

    const initLabels = labels.map(i=>{
        return {
            value: i
          }
        }
    )
    useEffect(()=>{
        
    },[])
    const handleSubmit= async (e) => {
      e.preventDefault();
      console.log(title, recurrence, date, time, priority, category, labels)

      const data = {title,recurrence,date,time,priority,category,labels};
      await post({urlExtension:`/reminders/${reminder.id}/update`,
          body:data,
          accessToken:auth.accessToken
      }).then(res => {
        navigate("/")
      }).catch(err =>{
        if(err.response.data){
          setErrMsg(err.response.data);
        }else {
          setErrMsg("Please enter all requried fields");
        }
      });
      
    }
  
    return (
      <div className='parent'>
        <form onSubmit={e => { handleSubmit(e) }} className='container'>
          <h1 className="title">Edit Reminder</h1>
          <label className='label'>Title</label>
          <br />
          <input 
            className='input'
            name='title' 
            type='text'
            value={title}
            onChange={e => setTitle(e.target.value)}
          />
          <br/>
          <label className='label'>Recurrence</label>
          <br />
          <select 
              className='input'
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
          <label className='label'>Cateogry</label>
          <br />
          <select 
              className='input'
              id="category"
              name="category"
              value={category}
              onChange={e=> setCategory(e.target.value)}>
              <option value="WORK">Work</option>
              <option value="PERSONAL">Personal</option>
              <option value="EDUCATION">Education</option>
          </select>
          <br />
          <label className='label'>Date</label>
          <br />
          <input
            className='input'
            name='date' 
            type='date'
            value={date}
            onChange={e => setDate(e.target.value)}
          />
          <br/>
          <label className='label'>Time</label>
          <br/>
          <input
            className='input'
            name='time' 
            type='time'
            value={time}
            onChange={e => setTime(e.target.value)}
          />
          <br/>
          <label className='label'>Priority</label>
          <br />
          <input
            className='input'
            name='priority' 
            type='number'
            min={1}
            value={priority}
            onChange={e => setPriority(e.target.value)}
          />
          <br/>       
        
          <br/>
          <Labels initLabels={initLabels} setLabels={setLabels}/>
          {/* <FileUploader setAttachment={setAttachment}/> */}
          <br/>
          {errMsg}
          <br/>
          <br/>

          <input 
            className='input'  
            type='submit' 
            value='Save Changes' 
          />
        </form>
      </div>
    )
  }

export default EditReminder;