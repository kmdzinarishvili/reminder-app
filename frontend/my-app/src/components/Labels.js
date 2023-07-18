
import {useState,useEffect} from 'react';

export default function Labels ({setLabels}) {
    const inputArr = [
    {
      type: "text",
      id: 1,
      value: ""
    }
  ];

  const [arr, setArr] = useState(inputArr);
  useEffect(()=>{
    const localLabels = arr.map(a => a.value)
    console.log(localLabels);
    setLabels(localLabels)
  },[arr])
  const addInput = () => {
    setArr(s => {
      return [
        ...s,
        {
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
            key={i+item}
            onChange={handleChange}
            value={item.value}
            id={i}
            type="text"
            size="40"
          />
        );
      })}
      <button style={{width:'28%'}} onClick={addInput}>+</button>
      {arr.length>1&&<button style={{width:'28%'}} onClick={removeInput}>-</button>}
    </div>
  );
}