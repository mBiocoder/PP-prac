{% include 'generic_header.html' %}
{%if ali_output %}
<div style="font-size:15px; position:relative;left:70px; top:100px; font-family: Helvetica, sans-serif">
	<b><h3>Result:</h3></b>
        <pre>{{ali_output}}</pre>
</div>

{% else %}
<div style="position: absolute; left: 10%; top: 0%;">  

<div style = "position:relative; left:70px; top:70px; font-family: Helvetica, sans-serif">
<form action="./validation_main.py" method="POST" enctype="multipart/form-data">
<b><label>Please upload your file:</label></b></br></br>
File: <input name = "file1" type = "file">
<p><b>Please input your reference and prediction alignments here:</b></p>
<textarea id="sequence1" name="sequence1" rows="5" cols="70">
</textarea><br><br>
<button type="submit">Submit</button>
</form>
</div></div>

{% endif %}

{% include 'generic_footer.html' %}
