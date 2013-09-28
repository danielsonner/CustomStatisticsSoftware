var submitData, resetData, i, ROUNDINGFIXER, MAX_STDEV_DECI;

submitData = function () {
  var data = $('#dataField').val();
  var dataArray = data.split(' ');
  // Convert everything to floats
  for (i = 0; i < dataArray.length; i++)
  {
    dataArray[i] = parseFloat(dataArray[i]);
  }
  // without giving it the comparator function the data set 1 2 3 10 6 5 bugs
  dataArray.sort(function(a,b) { return parseFloat(a) - parseFloat(b) });
  var lenDivTwo = Math.floor(dataArray.length/2);
  var useEvenQuartFindAlg = ((lenDivTwo)%2 === 0);
  
  // Create a 5 number summary
  var fiveNumSum = [];
  // Min
  fiveNumSum.push(dataArray[0]);
  // Q1
  if (useEvenQuartFindAlg)
  {
    // in javascript .1 + .2 != .3 so need to do weird rounding to make it behave
    fiveNumSum.push(Math.round((dataArray[Math.floor(dataArray.length/4) - 1] + 
      dataArray[Math.floor(dataArray.length/4)]) / 2 * ROUNDINGFIXER)/ROUNDINGFIXER);
  }
  else
  {
    fiveNumSum.push(dataArray[Math.floor(dataArray.length/3) -1]);
  }
  // M
  if (dataArray.length%2 === 0)
  {
    fiveNumSum.push(Math.round((dataArray[lenDivTwo] + 
      dataArray[(lenDivTwo) - 1])/2 * ROUNDINGFIXER) / ROUNDINGFIXER);
  }
  else
  {
    fiveNumSum.push(dataArray[lenDivTwo]);
  }
  // Q3
  if (useEvenQuartFindAlg)
  {
    fiveNumSum.push(Math.round((dataArray[dataArray.length -
      Math.floor(dataArray.length/4) - 1] + dataArray[dataArray.length - 
      Math.floor(dataArray.length/4)]) / 2 * ROUNDINGFIXER) / ROUNDINGFIXER);
  }
  else
  {
    fiveNumSum.push(dataArray[dataArray.length - Math.floor(dataArray.length/3)]);
  }
  // Max
  fiveNumSum.push(dataArray[dataArray.length - 1]);
  
  var sum = 0;
  // Calc the sum and stdev
  for (i = 0; i < dataArray.length; i++)
  {
    sum += dataArray[i];
  }
  
  var mean = sum / dataArray.length;
  
  sum = 0; 
  for (i = 0; i < dataArray.length; i++)
  {
    sum += (dataArray[i] - mean)*(dataArray[i] - mean);
  }
  
  // Limit stdev to 5 decimals
  var stdev = Math.round(Math.sqrt(sum / (dataArray.length - 1)) * MAX_STDEV_DECI) / MAX_STDEV_DECI;
  var IQR = Math.round((dataArray[3] - dataArray[1]) * ROUNDINGFIXER) / ROUNDINGFIXER;
  var range = Math.round((dataArray[4] - dataArray[0]) * ROUNDINGFIXER) / ROUNDINGFIXER;
  
  // Display the results in the output area
  var displayableHTML = 'Min: ' + fiveNumSum[0] + '<br /> Q1: ' + fiveNumSum[1] +
    '<br /> Median: ' + fiveNumSum[2] + '<br /> Q3: ' + fiveNumSum[3] +
    '<br /> Max: ' + fiveNumSum[4] + '<br /> Mean: ' + mean + 
    '<br /> Standard Deviation \(sample\):' + stdev+ '<br /> n \= ' + dataArray.length +
    '<br /> IQR: ' + IQR + '<br /> Range: ' + range +
    '<br /> <br /> <u> Copy Paste to Excel Formatted: </u> <br />';
  for (i = 0; i < 5; i++) 
  {
    displayableHTML += fiveNumSum[i] + '<br />';
  }
  displayableHTML += mean + '<br />' + stdev + '<br />' + dataArray.length
  + '<br />' + IQR + '<br />' + range;
  $('#outputArea').html(displayableHTML);
};

resetData = function () {
  $('#dataField').val('');
  $('#outputArea').html('');
};

$(document).ready(function(){
  // This variable is used because javascript has a poor 
  // implementation of small fractions ops (like .1 + .2)
  ROUNDINGFIXER = 100000;
  MAX_STDEV_DECI = 1000000;
  $('#submitData').click(submitData);
  $('#reset').click(resetData);
  
  // Bind the submit button the the enter key
  $(document).keyup(function(event){
      if(event.keyCode == 13) {
          $("#submitData").click();
      }
  });
});
