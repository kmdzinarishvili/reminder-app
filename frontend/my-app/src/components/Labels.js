
import {useState,useEffect} from 'react';

export default function Labels ({initLabels, setLabels}) {
  const [arr, setArr] = useState(initLabels||[{value:''}]);
  useEffect(()=>{
    const localLabels = arr.map(a => a.value)
    console.log(localLabels);
    setLabels(localLabels)
  },[arr])
  const addInput = (e) => {
    e.preventDefault()
    setArr(s => {
      return [
        ...s,
        {
          value: ""
        }
      ];
    });
  };
  const removeInput = (e) => {
    e.preventDefault()
    setArr(s => {
        return s.slice(0, -1);;
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
      <div className='label' style={{display:'flex', justifyContent:'center', alignItems:'center'}}>
        <label>Labels:</label>
      </div>
      {arr.map((item, i) => {
        console.log(item.value)
        return (
          <input
            style={{width:390}}
            className='input'
            key={i+item}
            onChange={handleChange}
            value={item.value}
            id={i}
            type="text"
            size="40"
            required={i===0}
          />
        );
      })}
      <div style={{display:'flex'}}>
       <button style={{width:100}} onClick={addInput}>+</button>
        {arr.length>1&&<button style={{width:100}} onClick={removeInput}>-</button>}
      </div>
    </div>
  );
}