FROM python:3
WORKDIR /opt

RUN pip install flask requests
COPY Density_map_data.py /opt/
COPY Controller_density_Map.py /opt/

EXPOSE 5000

CMD [ "python", "./Controller_density_Map.py" ]
