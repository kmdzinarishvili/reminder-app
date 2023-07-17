import React, { useEffect } from 'react';
import { useState } from 'react';
import useAuth from '../hooks/useAuth';
const Labels = ({setLabels}) => {
    const inputArr = [
    {
      type: "text",
      id: 1,
      value: ""
    }
  ];

  const [arr, setArr] = useState(inputArr);
  useEffect(()=>{
    // setLabels()
  },[])
  const addInput = () => {
    setArr(s => {
      return [
        ...s,
        {
          type: "text",
          value: ""
        }
      ];
    });
  };
  const removeInput = () => {
    setArr(s => {
        return arr.slice(0, -1);;
    })
  };

  const handleChange = e => {
    e.preventDefault();

    const index = e.target.id;
    setArr(s => {
      const newArr = s.slice();
      newArr[index].value = e.target.value;
      return newArr;
    });
  };

  return (
    <div className='box'>
      {arr.map((item, i) => {
        return (
          <input
            onChange={handleChange}
            value={item.value}
            id={i}
            type={item.type}
            size="40"
          />
        );
      })}
      <button style={{width:'28%'}} onClick={addInput}>+</button>
      {arr.length>1&&<button style={{width:'28%'}} onClick={removeInput}>-</button>}
    </div>
  );
}
const AddReminder = () =>{
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
    const [labelInputs, setLabelInputs] = useState();
    // private String userEmail;
    // private String userUsername;
    const { auth } = useAuth;
    const handleSubmit= (e) => {
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
      e.preventDefault();
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
        <Labels/>
        <input 
          type='submit' 
          value='Add Reminder' 
        />
      </form>
    )
  }

export default AddReminder;

//   import { useNavigate } from "react-router-dom";
// import { useState } from "react";
// import useAuth from "../hooks/useAuth";

// const AddReminder = () => {
//     const { auth } = useAuth;
//     const navigate = useNavigate();
//     const addReminder = () =>{
//         navigate('/add');
//     }
//     return (
//         <section className="page">
//         </section>
//     )
// }

// export default Home