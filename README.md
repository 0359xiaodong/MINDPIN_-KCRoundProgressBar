
圆形进度条组件</br>

       <declare-styleable name="KCRoundProgressBar">
        <attr name="text" format="string" />
        <attr name="text_color" format="color" />
        <attr name="text_size" format="dimension" />  
        
        <attr name="fg_width" format="dimension" />
        <attr name="fg_length" format="dimension" />
        <attr name="fg_color" format="color" />
        
        <attr name="border_color" format="color" />
        <attr name="border_width" format="dimension" />
        
        <attr name="bg_color" format="color" />
        <attr name="bg_width" format="dimension" />
        
        <attr name="roll_speed" format="dimension" />
        <attr name="roll_delay_millis" format="integer" />
        <attr name="radius" format="dimension" />
        
        <attr name="start_angle" format="integer" />
        <attr name="loading_mode" format="boolean" />
        <attr name="max_value" format="integer" />
        <attr name="min_value" format="integer" />  
        <attr name="text_display" format="boolean" />
        <attr name="progress" format="integer" />  
        
        <attr name="mode">
            <enum name="loading" value="0" />
            <enum name="progress" value="1" />
        </attr>
        
    </declare-styleable>



大部分控件属性，可以通过在xml和java文件中设置


