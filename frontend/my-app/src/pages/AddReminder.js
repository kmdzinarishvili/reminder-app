import { useState } from 'react';
import useAuth from '../hooks/useAuth';
import { post } from '../helper/post';
import { useNavigate } from 'react-router-dom';

import Labels from '../components/Labels';

const AddReminder = () =>{
    const navigate = useNavigate();

    const [title, setTitle] = useState();
    const [recurrence, setRecurrence] = useState('NEVER');
    const [date, setDate] = useState();
    const [time, setTime] = useState();
    const [priority, setPriority] = useState(1);
    const [category, setCategory] = useState('WORK');
    const [showUsername, setShowUsername] = useState(false);
    const [toSomeone, setToSomeone] = useState('');
    const [labels, setLabels] = useState();
    const [attachment, setAttachment] = useState();
    const { auth } = useAuth();
    const [errMsg, setErrMsg]=useState();
    
    const handleSubmit= async (e) => {
      e.preventDefault();
      console.log(title, recurrence, date, time, priority, category, labels)
      const data = {title,recurrence,date,time,priority,category,labels};
      if(showUsername){
        if(toSomeone.includes("@")){
          data['userEmail'] = toSomeone;
        }else{
          data['userUsername'] = toSomeone;
        }
      }
      await post({urlExtension:'/reminders',
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
  
    return (
      <div className='parent'>
      <form onSubmit={e => { handleSubmit(e) }} className='container'>
        <h3 className='title'>Add Reminder</h3>
        <label className='label' >Title:</label>
         <input 
          className='input'  
          name='title' 
          type='text'
          value={title}
          onChange={e => setTitle(e.target.value)}
        />
        <br/>
        <label className='label'>Recurrence:</label>
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
        <label className='label'>Cateogry:</label>
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
        <label className='label'>Date:</label>
         <input 
          className='input' 
          name='date' 
          type='date'
          value={date}
          onChange={e => setDate(e.target.value)}
        />
        <br/>
        <label className='label'>Time:</label>
         <input 
          className='input' 
          name='time' 
          type='time'
          value={time}
          onChange={e => setTime(e.target.value)}
        />
        <br/>
        <label className='label'>Priority:</label>
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
        <div className='label'>
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
        <label >Assign To Someone Else</label>
        </div>
        {showUsername&& <input 
          className='input'  
          name='title' 
          type='text'
          value={toSomeone}
          onChange={e => setToSomeone(e.target.value)}
        />
        }
        <br/>
        <Labels setLabels={setLabels}/>
        {/* <FileUploader setAttachment={setAttachment}/> */}
        <br/>
        {errMsg}
        <br/>
         <input 
          className='input'  
          type='submit' 
          value='Add Reminder' 
        />
      </form>
      </div>
    )
  }

export default AddReminder;